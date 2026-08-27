/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * *****************************************************************************
 * File name:   KnPAMAccInfoDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          08/02/2013       7.4
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
public class KnPAMAccInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622678000L;

    //stores the PAM Account Id
    private int pamAccId;
    //stores the PAM Account Name
    private String billingName;
    //stores the PAM Account State
    private int pamAccState;
    // stores the External PAM Account Id
    private String extPamAccId;
    //stores the Billing MDN
    private String billingMdn;
    //stores the Max Subs
    private int totalNoOfLines;
    //stores the Subscriber count
    private int subscriberCount;
    //stores the creation time
    private long creationTime;
    //stores the Last update time
    private long lastUpdateTime;
    //stores old billing MDN.
    private String OldBillingNumber;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private KnPAMSubsProfInfoDTO profileDetails;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getOldBillingNumber() {
		return OldBillingNumber;
	}

	public void setOldBillingNumber(String oldBillingNumber) {
		OldBillingNumber = oldBillingNumber;
	}

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;

    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public int getPamAccState() {
        return pamAccState;
    }

    public void setPamAccState(int pamAccState) {
        this.pamAccState = pamAccState;
    }

    public String getExtPamAccId() {
        return extPamAccId;
    }

    public void setExtPamAccId(String extPamAccId) {
        this.extPamAccId = extPamAccId;
    }

    public String getBillingMdn() {
        return billingMdn;
    }

    public void setBillingMdn(String billingMdn) {
        this.billingMdn = billingMdn;
    }

    public int getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public void setTotalNoOfLines(int totalNoOfLines) {
        this.totalNoOfLines = totalNoOfLines;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getObjectId() {
        return String.valueOf(this.pamAccId);
    }

    public KnPAMSubsProfInfoDTO getProfileDetails() {
        return profileDetails;
    }

    public void setProfileDetails(KnPAMSubsProfInfoDTO profileDetails) {
        this.profileDetails = profileDetails;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[KnPAMAccInfoDTO --> ")
        		.append(", pamAccId - ").append(pamAccId)
                .append(", pamAccName - ").append(billingName)
                .append(", pamAccState - ").append(pamAccState)
                .append(", extPamAccId - ").append(extPamAccId)
                .append(", billingMdn - ").append(KnGDPRTemplate.mdn(billingMdn))
                .append(", maxSubscriber - ").append(totalNoOfLines)
                .append(", subscriberCount - ").append(subscriberCount)
                .append(", creationTime - ").append(creationTime)
                .append(", lastUpdateTime - ").append(lastUpdateTime)
                 .append(", profileDetails  - ").append(profileDetails)
                 .append(", hierarchyType  - ").append(hierarchyType)
                .append("]");

        return builder.toString();
    }
}
