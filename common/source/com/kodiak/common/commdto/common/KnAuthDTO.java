/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMAuthDTO.java
 * Subsystem:   Common DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/29/10       7.0
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
package com.kodiak.common.commdto.common;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnAuthDTO implements IAuthDTO {

    private static final long serialVersionUID = 7526471155622676120L;

    private String userId;
    private String password;
    private String tokenId;

    private String objectId;

    /**
     * getter method for the UserId
     *
     * @return String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * setter method for the User Id
     *
     * @param userId sets userid to the subscriber
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * getter method for the password
     *
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * setter method for the password
     *
     * @param password set password to the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * getter method for Token Id
     *
     * @return String
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * setter method for the Token Id
     *
     * @param tokenId the tokenId
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * getter method for the Object Id
     *
     * @return String
     */
    public String getObjectId() {
        return objectId;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" User_Id - ").append(KnGDPRTemplate.userId(userId))
                .append(", Password - ").append(password)
                .append(", Token_Id - ").append(tokenId)
                .append(", Object_Id - ").append(objectId);

        return strBuffer.toString();

    }
}
