/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;

/**
 * ************************************************************************
 * <p/>
 * File name:  ISubscriberDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
public interface ISubscriberDTO extends IGenDTO {

    /**
     * sets the mdn to the subscriber
     *
     * @param mdn sets mdn to the subscriber
     */
    void setMdn(String mdn);

    /**
     * returns the mdn of the subscriber
     *
     * @return mdn  returns mdn of a subscriber
     */
    String getMdn();

    /**
     * sets the name to the subscriber
     *
     * @param networkName sets name to the subscriber
     */
    void setNetworkName(String networkName);

    /**
     * returns name of the subscriber
     *
     * @return name returns name of a subscriber
     */
    String getNetworkName();

    /**
     * sets the pubSubscription type to the mdn
     *
     * @param pubSubscriptionType sets pubSubscription type for a mdn
     */
    void setPubSubscriptionType(int pubSubscriptionType);

    /**
     * returns the pubSubscription type of mdn
     *
     * @return mdn  returns pubSubscription type of mdn
     */
    int getPubSubscriptionType();

    /**
     *
     * @param corpSubscriptionType
     */
    void setCorpSubscriptionType(int corpSubscriptionType);

    /**
     *
     * @return
     */
    int getCorpSubscriptionType();

    /**
     * sets the subscriber service status
     *
     * @param serviceAuthStatus sets service status to subscriber
     */
    void setServiceAuthStatus(int serviceAuthStatus);

    /**
     * returns the subscriber service status
     *
     * @return serviceAuthStatus   returns service status of an subscriber
     */
    int getServiceAuthStatus();

}
