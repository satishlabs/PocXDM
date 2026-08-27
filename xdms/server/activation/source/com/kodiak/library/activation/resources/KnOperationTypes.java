/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnOperationTypes.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
package com.kodiak.library.activation.resources;

public class KnOperationTypes {
    // Client Management APIs - register & deregister
    public static final String REGISTER_CLIENT = "registerClient";
    // CSR APIs
    public static final String FORCE_DATA_SYNC = "forceDataSync";
    
    public static final String RETRIEVE_MDN = "retrieveMDN";
    public static final String DEVICE_ACTIVATION = "deviceActivation";
    public static final String RADIO_DEVICE_ACTIVATION = "radiodeviceActivation";
    public static final String USER_LOGIN = "userLogin";
}
