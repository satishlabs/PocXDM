/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;

import com.kodiak.common.dto.IIdentifier;

/**
 * *********************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/10/11   7.0
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
 * *************************************************************************
 */
public interface IAuthDTO extends IIdentifier {

    /**
     * sets the user id
     *
     * @param userId sets userid to the subscriber
     */
    public void setUserId(String userId);

    /**
     * returns the unique key
     *
     * @return userid of a subscriber
     */
    public String getUserId();

    /**
     * changes the current password of subscriber
     *
     * @param password set password to the user
     */
    public void setPassword(String password);

    /**
     * returns the current password of subscriber
     *
     * @return password of an subscriber
     */
    public String getPassword();

    /**
     * This method returns the tokenId
     *
     * @return tokenId
     */
    public String getTokenId();

    /**
     * This method sets the tokenId
     *
     * @param tokenId the tokenId
     */
    public void setTokenId(String tokenId);
}
