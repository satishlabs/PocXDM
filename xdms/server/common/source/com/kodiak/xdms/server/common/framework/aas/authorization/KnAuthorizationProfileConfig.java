/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAuthorizationProfileConfig.java
 * Subsystem:   Framework 
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      04-05-2006 5.7
 *
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 **************************************************************************/
package com.kodiak.xdms.server.common.framework.aas.authorization;

/**
 *
 */
public class KnAuthorizationProfileConfig {

    IProfileAuthorizer profileAuthorizer;

    /**
     * returns the profile Authorization entity object
     *
     * @return Authorization entity object
     */
    public IProfileAuthorizer getProfileAuthorizer() {
        return profileAuthorizer;
    }

    /**
     * set profile authorization entity object
     *
     * @param authProfile the authorization entity
     */
    public void setProfileAuthorizer(IProfileAuthorizer authProfile) {
        profileAuthorizer = authProfile;
    }
}
