/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthenticationProfileConfig.java
 * Subsystem:   WGP
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      19/10/2006 5.7
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
 ***************************************************************************/
package com.kodiak.xdms.server.common.framework.aas.authentication;

import java.util.Map;

public class KnAuthenticationProfileConfig {

    private String allowed = "false";

    // stores the OperationConfig objects
    private Map authenticationOperationConfigMap;

    /**
     * Constructor
     */
    public KnAuthenticationProfileConfig(){

    }

    public String getAllowed(){
        return allowed;
    }

    public void setAllowed(String allowed){
        this.allowed = allowed;
    }


    /**
     * this method for getting the proper OperationConfig object
     *
     * @param key
     * @return OperationConfig object depending on the value of the key
     */
    public KnAuthenticationOperationConfig getOperationConfig(String key){
        return (KnAuthenticationOperationConfig)authenticationOperationConfigMap.get(key);
    }

    /**
     * this method sets the authenticationOperationConfigMap
     *
     * @param authenticationOperationConfigMap
     */
    public void setOperationConfigMap(Map authenticationOperationConfigMap){
        this.authenticationOperationConfigMap = authenticationOperationConfigMap;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append("KnAuthenticationProfileConfig{").append("Allowed: ").append(allowed)
                .append(", OperationConfig: ").append(authenticationOperationConfigMap).append("}");
        return buffer.toString();
    }
}
