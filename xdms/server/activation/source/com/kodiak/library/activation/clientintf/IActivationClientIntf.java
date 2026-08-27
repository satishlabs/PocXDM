/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IActivationClientIntf.java
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
package com.kodiak.library.activation.clientintf;

/**
 * This interface is the entry point to the Activation Client management library. This interface has a
 * super set of methods from all the client interfaces. This interface will deligate
 * the method calls to corresponding implementations.
 * <BR>The client can have an access to all the APIs through a direct reference of API with this interface or by accessing
 * individual client interfaces which is been segragated as per the different client functionalities.
 * To get an access to individual client interfaces, use the getter methods for different manager classes which in
 * turn provides the reference of APIs corresponds to that particular client interface.
 */
public interface IActivationClientIntf extends IActClientManager {
}
