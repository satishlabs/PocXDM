/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnProfileNotifyDTO.java
 * Subsystem:   XCAP Notification Mgr
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       2/18/11       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.notificationmgr.beans;

import com.kodiak.common.dto.IIdentifier;

public class KnProfileNotifyDTO extends KnCommonNotifyDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676249L;
    private String clientTypeChange;
    private String subscriptionTypeChange;
    private String activeFeatureSetChange;
    private String subscriberNameChange;
    private String credentialChange;
    private String objectId;
    private String newClientTypeChange;
    private String lastProfileUpdateTimestamp;

    public String getClientTypeChange() {
        return clientTypeChange;
    }

    public void setClientTypeChange(String clientTypeChange) {
        this.clientTypeChange = clientTypeChange;
    }

    public String getSubscriptionTypeChange() {
        return subscriptionTypeChange;
    }

    public void setSubscriptionTypeChange(String subscriptionTypeChange) {
        this.subscriptionTypeChange = subscriptionTypeChange;
    }

    public String getActiveFeatureSetChange() {
        return activeFeatureSetChange;
    }

    public void setActiveFeatureSetChange(String activeFeatureSetChange) {
        this.activeFeatureSetChange = activeFeatureSetChange;
    }

    public String getSubscriberNameChange() {
        return subscriberNameChange;
    }

    public void setSubscriberNameChange(String subscriberNameChange) {
        this.subscriberNameChange = subscriberNameChange;
    }

    public String getCredentialChange() {
        return credentialChange;
    }

    public void setCredentialChange(String credentialChange) {
        this.credentialChange = credentialChange;
    }

    public String getNewClientTypeChange() {
        return newClientTypeChange;
    }

    public void setNewClientTypeChange(String newClientTypeChange) {
        this.newClientTypeChange = newClientTypeChange;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getLastProfileUpdateTimestamp() {
        return lastProfileUpdateTimestamp;
    }

    public void setLastProfileUpdateTimestamp(String lastProfileUpdateTimestamp) {
        this.lastProfileUpdateTimestamp = lastProfileUpdateTimestamp;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" KnProfileNotifyDTO --> ");
        strBuffer.append(", CLIENT_TYPE_CHANGE - ").append(clientTypeChange)
                .append(", SUBS_TYPE_CHANGE - ").append(subscriptionTypeChange)
                .append(", ACTIVE_FS_CHANGE - ").append(activeFeatureSetChange)
                .append(", SUBS_NAME_CHANGE - ").append(subscriberNameChange)
                .append(", newClientTypeChange - ").append(newClientTypeChange)
                .append(", lastProfileUpdateTimestamp - ").append(lastProfileUpdateTimestamp);
        return strBuffer.toString();
    }
}
